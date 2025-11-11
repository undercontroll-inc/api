package com.undercontroll.api.adapters;

import com.undercontroll.api.dto.ServiceOrderDto;
import com.undercontroll.api.service.PdfExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.interactive.form.*;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class PdfBoxService implements PdfExportService {

    private final ResourceLoader resourceLoader;

    @Override
    public void exportOS(ServiceOrderDto serviceOrder) {
//        try {
//            Resource resource = resourceLoader.getResource("classpath:os/os_model.pdf");
//            InputStream inputStream = resource.getInputStream();
//
//            PDDocument document = Loader.loadPDF(inputStream.readAllBytes());
//
//            PDDocumentCatalog catalog = document.getDocumentCatalog();
//            PDAcroForm acroForm = catalog.getAcroForm();
//
//            if (acroForm == null) {
//                log.error("PDF não possui formulário (AcroForm)");
//                document.close();
//                return;
//            }
//
//            for (Map.Entry<String, String> entry : dados.entrySet()) {
//                String nomeCampo = entry.getKey();
//                String valor = entry.getValue();
//
//                PDField field = acroForm.getField(nomeCampo);
//
//                if (field == null) {
//                    log.warn("Campo '{}' não encontrado no PDF", nomeCampo);
//                    continue;
//                }
//
//                preencherCampo(field, valor);
//            }
//
//            try {
//                acroForm.refreshAppearances();
//            } catch (Exception e) {
//                acroForm.setNeedAppearances(true);
//            }
//
//            document.save(outputPath);
//            document.close();
//
//            log.info("PDF preenchido e salvo em: {}", outputPath);
//
//        } catch (IOException e) {
//            log.error("Erro ao preencher PDF", e);
//            throw new RuntimeException("Erro ao processar PDF", e);
//        }
    }

    private void preencherCampo(PDField field, String valor) {
        try {
            if (field instanceof PDTextField) {
                field.setValue(valor);

            } else if (field instanceof PDCheckBox) {
                PDCheckBox checkbox = (PDCheckBox) field;
                if ("true".equalsIgnoreCase(valor) || "yes".equalsIgnoreCase(valor) || "1".equals(valor)) {
                    checkbox.check();
                } else {
                    checkbox.unCheck();
                }

            } else if (field instanceof PDRadioButton) {
                PDRadioButton radioButton = (PDRadioButton) field;
                radioButton.setValue(valor);

            } else if (field instanceof PDComboBox) {
                field.setValue(valor);

            } else {
                // Outros tipos - tenta setValue genérico
                field.setValue(valor);
            }

            log.debug("Campo '{}' preenchido com: {}", field.getFullyQualifiedName(), valor);

        } catch (Exception e) {
            log.error("Erro ao preencher campo '{}': {}", field.getFullyQualifiedName(), e.getMessage());
        }
    }

    private void listarCamposRecursivo(PDField field, String prefixo) {
        String nome = field.getFullyQualifiedName();
        String tipo = field.getClass().getSimpleName();
        String valor = field.getValueAsString();

        log.info("{}Campo: {} | Tipo: {} | Valor atual: {}", prefixo, nome, tipo, valor);

        if (field instanceof PDNonTerminalField) {
            PDNonTerminalField parent = (PDNonTerminalField) field;
            for (PDField child : parent.getChildren()) {
                listarCamposRecursivo(child, prefixo + "  ");
            }
        }
    }

    private void listFields(List<PDField> fields, String prefix) {
        for (PDField f : fields) {
            String fullName = prefix == null || prefix.isEmpty() ? f.getFullyQualifiedName() : prefix + "." + f.getFullyQualifiedName();
            System.out.println(" - " + fullName + "  (tipo: " + f.getClass().getSimpleName() + ")");

            // Se o campo for um "non-terminal" (ou seja, tem filhos), faz cast e itera recursivamente
            if (f instanceof PDNonTerminalField) {
                PDNonTerminalField nonTerm = (PDNonTerminalField) f;
                List<PDField> kids = nonTerm.getChildren();
                if (kids != null && !kids.isEmpty()) {
                    listFields(kids, fullName);
                }
            }
        }
    }
}
