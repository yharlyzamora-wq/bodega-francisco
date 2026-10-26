package pe.bodegafrancisco.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import pe.bodegafrancisco.model.Producto;

class ExportacionServiceTest {

    @Test
    void generaExcelConEncabezadosYUnaFilaPorProducto() throws Exception {
        var productos = List.of(
                new Producto(1, "PE-0001", "Arroz Faraón 5kg", "Abarrotes", new BigDecimal("22.50"), 18, true),
                new Producto(2, "PE-0002", "Helado Donofrio 1L", "Congelados", new BigDecimal("45.90"), 3, true),
                new Producto(3, "PE-0003", "Detergente", "Limpieza", BigDecimal.TEN, 0, false));
        var servicio = new ExportacionService(() -> productos);

        byte[] datos = servicio.stockProductosXlsx();

        try (var libro = new XSSFWorkbook(new ByteArrayInputStream(datos))) {
            var hoja = libro.getSheetAt(0);
            assertEquals("Código", hoja.getRow(0).getCell(0).getStringCellValue());
            assertEquals(4, hoja.getPhysicalNumberOfRows());
            assertEquals("PE-0001", hoja.getRow(1).getCell(0).getStringCellValue());
            assertEquals(22.5, hoja.getRow(1).getCell(3).getNumericCellValue(), 0.001);
            assertEquals(18, (int) hoja.getRow(1).getCell(4).getNumericCellValue());
            assertEquals("Stock bajo", hoja.getRow(2).getCell(5).getStringCellValue());
            assertEquals("Inactivo", hoja.getRow(3).getCell(5).getStringCellValue());
        }
    }
}