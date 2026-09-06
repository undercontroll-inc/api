package com.undercontroll.domain.usecase.chat.impl;

import com.undercontroll.application.dto.chat.ChatSuggestionsResponse;
import com.undercontroll.domain.enums.OrderStatus;
import com.undercontroll.domain.gateway.AnaSuggestionStore;
import com.undercontroll.domain.gateway.AnnouncementGateway;
import com.undercontroll.domain.gateway.ComponentGateway;
import com.undercontroll.domain.gateway.CurrentUserIdPort;
import com.undercontroll.domain.gateway.DemandGateway;
import com.undercontroll.domain.gateway.OrderGateway;
import com.undercontroll.domain.model.Announcement;
import com.undercontroll.domain.model.ComponentPart;
import com.undercontroll.domain.model.Demand;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.model.OrderItem;
import com.undercontroll.domain.model.User;
import com.undercontroll.domain.model.chat.ShopSnapshot;
import com.undercontroll.domain.model.chat.ShopSuggestionComposer;
import com.undercontroll.infrastructure.config.AnaProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetChatSuggestionsImplTest {

    @Mock
    private AnaSuggestionStore anaSuggestionStore;

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private ComponentGateway componentGateway;

    @Mock
    private DemandGateway demandGateway;

    @Mock
    private AnnouncementGateway announcementGateway;

    @Mock
    private CurrentUserIdPort currentUserIdPort;

    private GetChatSuggestionsImpl useCase;

    @BeforeEach
    void setUp() {
        AnaProperties properties = new AnaProperties();
        properties.setSuggestionCount(4);
        ShopSnapshotLoader loader = new ShopSnapshotLoader(
                orderGateway,
                componentGateway,
                demandGateway,
                announcementGateway
        );
        useCase = new GetChatSuggestionsImpl(
                anaSuggestionStore,
                loader,
                properties,
                currentUserIdPort
        );
        when(currentUserIdPort.require()).thenReturn(3);
    }

    @Test
    @DisplayName("returns cached suggestions without hitting the database")
    void cacheHit() {
        when(anaSuggestionStore.findByUserId(3)).thenReturn(Optional.of(List.of("Pergunta cache")));

        ChatSuggestionsResponse response = useCase.execute(false);

        assertEquals(List.of("Pergunta cache"), response.suggestions());
        verify(orderGateway, never()).findOpenRepairs(anyInt());
    }

    @Test
    @DisplayName("builds grounded suggestions from limited shop queries without an LLM")
    void cacheMissUsesShopFacts() {
        stubShop();
        when(anaSuggestionStore.findByUserId(3)).thenReturn(Optional.empty());

        ChatSuggestionsResponse response = useCase.execute(false);

        assertEquals(4, response.suggestions().size());
        String joined = String.join(" ", response.suggestions());
        assertTrue(joined.contains("Maria"));
        assertTrue(joined.contains("Resistência"));
        assertTrue(joined.contains("Capacitor"));
        assertTrue(joined.contains("Feriado"));
        verify(orderGateway, never()).findAll();
        verify(componentGateway, never()).findAll();
        verify(demandGateway, never()).findAll();
        verify(anaSuggestionStore).save(eq(3), eq(response.suggestions()));
    }

    @Test
    @DisplayName("refresh regenerates from the current shop snapshot")
    void refresh() {
        stubShop();
        when(anaSuggestionStore.findByUserId(3)).thenReturn(Optional.of(List.of("Antiga")));

        ChatSuggestionsResponse response = useCase.execute(true);

        assertTrue(response.suggestions().getFirst().contains("Maria")
                || String.join(" ", response.suggestions()).contains("Maria"));
        verify(anaSuggestionStore).save(eq(3), eq(response.suggestions()));
    }

    @Test
    @DisplayName("falls back to generic questions when the shop has no facts")
    void emptyShop() {
        when(orderGateway.findOpenRepairs(ShopSuggestionComposer.OPEN_LIMIT)).thenReturn(List.of());
        when(orderGateway.findReadyForPickup(ShopSuggestionComposer.PICKUP_LIMIT)).thenReturn(List.of());
        when(componentGateway.findLowStock(
                ShopSuggestionComposer.LOW_STOCK_MAX,
                ShopSuggestionComposer.STOCK_LIMIT
        )).thenReturn(List.of());
        when(demandGateway.findRecent(ShopSuggestionComposer.DEMAND_LIMIT)).thenReturn(List.of());
        when(announcementGateway.findLastAnnouncement()).thenReturn(Optional.empty());
        when(anaSuggestionStore.findByUserId(3)).thenReturn(Optional.empty());

        ChatSuggestionsResponse response = useCase.execute(false);

        assertEquals(ShopSuggestionComposer.groundedQuestions(ShopSnapshot.empty(), 4), response.suggestions());
    }

    private void stubShop() {
        when(orderGateway.findOpenRepairs(ShopSuggestionComposer.OPEN_LIMIT)).thenReturn(List.of(
                Order.builder()
                        .id(12)
                        .status(OrderStatus.PENDING)
                        .user(User.builder().name("Maria").lastName("Souza").build())
                        .orderItems(List.of(
                                OrderItem.builder().type("liquidificador").brand("Mondial").build()
                        ))
                        .build()
        ));
        when(orderGateway.findReadyForPickup(ShopSuggestionComposer.PICKUP_LIMIT)).thenReturn(List.of());
        when(componentGateway.findLowStock(
                ShopSuggestionComposer.LOW_STOCK_MAX,
                ShopSuggestionComposer.STOCK_LIMIT
        )).thenReturn(List.of(
                ComponentPart.builder().name("Resistência").quantity(2L).build()
        ));
        when(demandGateway.findRecent(ShopSuggestionComposer.DEMAND_LIMIT)).thenReturn(List.of(
                Demand.builder()
                        .quantity(1L)
                        .component(ComponentPart.builder().name("Capacitor").build())
                        .order(Order.builder().id(8).build())
                        .build()
        ));
        when(announcementGateway.findLastAnnouncement()).thenReturn(Optional.of(
                Announcement.builder().title("Feriado").build()
        ));
    }
}
