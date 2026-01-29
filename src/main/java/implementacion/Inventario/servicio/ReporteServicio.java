package implementacion.Inventario.servicio;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import implementacion.Inventario.modelo.AjusteInventario;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.List;

@Service
public class ReporteServicio {

    //PDF
    public void exportarPDF(List<AjusteInventario> lista, String titulo) {

        try {

            String nombreArchivo = "Reporte_" + System.currentTimeMillis() + ".pdf";
            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));

            documento.open();

            //Titulo
            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);

            documento.add(new Paragraph(titulo, fuenteTitulo));
            documento.add(new Paragraph("Generado el: " + new java.util.Date()));
            documento.add(new Paragraph(" ")); // Espacio vacío

            PdfPTable tabla = new PdfPTable(4); // 4 Columnas
            tabla.setWidthPercentage(100);

            // Encabezados
            String[] headers = {"ID", "Producto", "Cantidad", "Motivo"};

            for (String h : headers) {

                PdfPCell celda = new PdfPCell(new Phrase(h));
                celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
                tabla.addCell(celda);

            }

            // Datos
            for (AjusteInventario a : lista) {

                tabla.addCell(String.valueOf(a.getId()));
                tabla.addCell(a.getProducto().getNombre());
                tabla.addCell(String.valueOf(a.getCantidadAjustada()));
                tabla.addCell(a.getMotivo());

            }

            documento.add(tabla);
            documento.close();

            System.out.println("PDF Generado exitosamente: " + nombreArchivo);

        } catch (Exception e) {

            System.err.println("Error creando PDF: " + e.getMessage());

        }
    }

    //Excel
    public void exportarExcel(List<AjusteInventario> lista, String titulo) {

        try (Workbook workbook = new XSSFWorkbook()) {

            String nombreArchivo = "Reporte_" + System.currentTimeMillis() + ".xlsx";
            Sheet sheet = workbook.createSheet("Reporte Inventario");

            //Encabezado
            Row headerRow = sheet.createRow(0);
            String[] columnHeaders = {"ID", "Producto", "Categoría", "Ubicación", "Cantidad", "Motivo", "Fecha"};

            for (int i = 0; i < columnHeaders.length; i++) {

                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnHeaders[i]);

            }

            // Llenar Datos
            int rowNum = 1;

            for (AjusteInventario a : lista) {

                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(a.getId());
                row.createCell(1).setCellValue(a.getProducto().getNombre());
                row.createCell(2).setCellValue(a.getProducto().getCategoria());
                row.createCell(3).setCellValue(a.getProducto().getUbicacion());
                row.createCell(4).setCellValue(a.getCantidadAjustada());
                row.createCell(5).setCellValue(a.getMotivo());
                row.createCell(6).setCellValue(a.getFecha().toString());

            }

            for(int i = 0; i < columnHeaders.length; i++) {

                sheet.autoSizeColumn(i);

            }

            // Guardar
            try (FileOutputStream fileOut = new FileOutputStream(nombreArchivo)) {

                workbook.write(fileOut);
                System.out.println("Excel Generado exitosamente: " + nombreArchivo);

            }

        } catch (Exception e) {

            System.err.println("Error creando Excel: " + e.getMessage());

        }
    }
}