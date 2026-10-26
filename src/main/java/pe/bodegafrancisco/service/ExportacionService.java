package pe.bodegafrancisco.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.bodegafrancisco.dao.ProductoDao;
import pe.bodegafrancisco.model.Producto;

/** Exporta reportes a Excel (Apache POI). Los textos se escriben como valores, nunca como fórmulas. */
@Service
public class ExportacionService {

    private static final Logger log = LoggerFactory.getLogger(ExportacionService.class);
    private static final String[] ENCABEZADOS = {"Código", "Producto", "Categoría", "Precio (S/)", "Stock", "Estado"};
    private static final int[] ANCHOS = {12, 38, 18, 14, 10, 14};

    private final ProductoDao productoDao;

    public ExportacionService(ProductoDao productoDao) {
        this.productoDao = productoDao;
    }

    /** @return archivo .xlsx con el stock de todos los productos. */
    public byte[] stockProductosXlsx() {
        List<Producto> productos = productoDao.listarTodos();
        try (var libro = new XSSFWorkbook(); var salida = new ByteArrayOutputStream()) {
            Sheet hoja = libro.createSheet("Stock");

            Font negrita = libro.createFont();
            negrita.setBold(true);
            negrita.setColor(IndexedColors.WHITE.getIndex());
            CellStyle estiloEncabezado = libro.createCellStyle();
            estiloEncabezado.setFont(negrita);
            estiloEncabezado.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
            estiloEncabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            CellStyle estiloPrecio = libro.createCellStyle();
            estiloPrecio.setDataFormat(libro.createDataFormat().getFormat("#,##0.00"));

            Row cabecera = hoja.createRow(0);
            for (int i = 0; i < ENCABEZADOS.length; i++) {
                var celda = cabecera.createCell(i);
                celda.setCellValue(ENCABEZADOS[i]);
                celda.setCellStyle(estiloEncabezado);
                hoja.setColumnWidth(i, ANCHOS[i] * 256);
            }
            int fila = 1;
            for (Producto p : productos) {
                Row r = hoja.createRow(fila++);
                r.createCell(0).setCellValue(p.codigo());
                r.createCell(1).setCellValue(p.nombre());
                r.createCell(2).setCellValue(p.categoria());
                var precio = r.createCell(3);
                precio.setCellValue(p.precio().doubleValue());
                precio.setCellStyle(estiloPrecio);
                r.createCell(4).setCellValue(p.stock());
                r.createCell(5).setCellValue(estado(p));
            }
            hoja.createFreezePane(0, 1);
            libro.write(salida);
            log.info("Excel de stock generado con {} productos", productos.size());
            return salida.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo generar el Excel", e);
        }
    }

    private static String estado(Producto p) {
        if (!p.activo()) return "Inactivo";
        if (p.stock() == 0) return "Agotado";
        return p.stockBajo() ? "Stock bajo" : "Activo";
    }
}