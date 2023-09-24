package com.example.erpapp.Classes;
import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import com.example.erpapp.Classes.Product;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SalesReceiptPDFGenerator extends PrintDocumentAdapter {
    private Context context;
    private List<Product> salesList;

    public SalesReceiptPDFGenerator(Context context, List<Product> salesList) {
        this.context = context;
        this.salesList = salesList;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, PrintDocumentAdapter.LayoutResultCallback callback, Bundle extras) {
        // Configure the layout of the printed document here (e.g., page size, margins)

        // Create a PrintDocumentInfo object to describe the document
        PrintDocumentInfo info = new PrintDocumentInfo.Builder("SalesReceipt.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(1) // Assuming one page for the receipt
                .build();

        // Return the PrintDocumentInfo to the system
        callback.onLayoutFinished(info, newAttributes.equals(oldAttributes));
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        // Generate the content of the receipt and write it to the PDF file
        Document document = new Document(PageSize.A4, 50, 50, 50, 50); // Set margins

        try {
            // Get a FileOutputStream for the PDF file
            FileOutputStream outputStream = new FileOutputStream(destination.getFileDescriptor());

            // Create a PdfWriter instance
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            // Open the document for writing
            document.open();

            // Create a font with an increased font size
            Font font = new Font(Font.FontFamily.TIMES_ROMAN, 25, Font.NORMAL);
            Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 25, Font.BOLDITALIC|Font.UNDEFINED);

            // Center align the content
            Paragraph header = new Paragraph("Receipt for Sale", font);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            // Add the current date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            String currentDateAndTime = dateFormat.format(new Date());
            Paragraph dateTime = new Paragraph("Date and Time: " + currentDateAndTime, font);
            dateTime.setAlignment(Element.ALIGN_CENTER);
            document.add(dateTime);

            Paragraph divider = new Paragraph("------------------------------", font);
            divider.setAlignment(Element.ALIGN_CENTER);
            document.add(divider);

            for (Product product : salesList) {
                Paragraph line = new Paragraph(
                        product.getProduct_name() + " x " + product.getQuantity() +
                                " = Ugx" + (product.getPrice() * product.getQuantity()), font);
                line.setAlignment(Element.ALIGN_CENTER);
                document.add(line);
            }

            // Add total to the PDF
            Paragraph totalLine = new Paragraph("Total: Ugx" + calculateTotalPrice(), font);
            totalLine.setAlignment(Element.ALIGN_CENTER);
            document.add(totalLine);

            divider.setAlignment(Element.ALIGN_CENTER);
            document.add(divider);

            // Center align the footer
            Paragraph footer = new Paragraph("**** Thanks for shopping with us ****", font);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            // Close the document
            document.close();

            // Notify the system that writing is complete
            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    private double calculateTotalPrice() {
        double total = 0;
        for (Product product : salesList) {
            total += product.getPrice() * product.getQuantity();
        }
        return total;
    }
}
