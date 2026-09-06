package com.undercontroll.domain.model.insight;

public enum InsightType {
    TENDENCIA_ALTA,
    OPORTUNIDADE_ESTOQUE,
    ALERTA_PRECO,
    OPORTUNIDADE_REPARO,
    DESTAQUE_MARCA,
    ALERTA_MERCADO,
    RECOMENDACAO;

    public static boolean isAllowed(String raw) {
        if (raw == null || raw.isBlank()) {
            return false;
        }
        try {
            valueOf(raw);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public static String normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            return raw;
        }
        return switch (raw) {
            case "PRECO_ALTA", "PRECO_QUEDA", "DISPERSAO_PRECO" -> ALERTA_PRECO.name();
            case "OPORTUNIDADE_ESTOQUE" -> OPORTUNIDADE_ESTOQUE.name();
            case "PRODUTO_ALTA" -> TENDENCIA_ALTA.name();
            case "MARCA" -> DESTAQUE_MARCA.name();
            case "CONCENTRACAO_CONCORRENCIA" -> ALERTA_MERCADO.name();
            case "PERFIL_TECNICO", "CATEGORIA_NAO_ATENDIDA" -> OPORTUNIDADE_REPARO.name();
            default -> isAllowed(raw) ? raw : raw;
        };
    }
}
