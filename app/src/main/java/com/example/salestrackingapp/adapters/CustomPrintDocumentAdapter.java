package com.example.salestrackingapp.adapters;

import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.util.Log;
import android.view.View;

import java.io.FileOutputStream;
import java.io.IOException;

public class CustomPrintDocumentAdapter extends PrintDocumentAdapter {
    private View contentView;
    private String jobName;

    private static final int PAGE_WIDTH = 595; // A4 size: 595 points = 8.27 inches
    private static final int PAGE_HEIGHT = 842; // A4 size: 842 points = 11.69 inches

    public CustomPrintDocumentAdapter(View contentView, String jobName) {
        this.contentView = contentView;
        this.jobName = jobName;
    }



    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        // Respond to cancellation request
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        // Set a fixed page size (A4 size as an example)
        PrintAttributes.MediaSize mediaSize = PrintAttributes.MediaSize.ISO_A4;
        PrintAttributes attributes = new PrintAttributes.Builder()
                .setMediaSize(mediaSize)
                .build();

        // Create PrintDocumentInfo with the fixed page size
        PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder(jobName);
        builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(1) // For simplicity, consider a single page layout
                ;

        PrintDocumentInfo info = builder.build();
        callback.onLayoutFinished(info, true);
    }



    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        // Start the PDF document
        PdfDocument document = new PdfDocument();

        // Set a fixed page size
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        // Draw the content of the view on the PDF page
        Canvas canvas = page.getCanvas();
        contentView.draw(canvas);

        // Finish the page
        document.finishPage(page);

        // Write the PDF document to the destination file
        try {
            document.writeTo(new FileOutputStream(destination.getFileDescriptor()));
            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
        } catch (IOException e) {
            Log.e("PrintError", "Error writing PDF: " + e.getMessage());

            callback.onWriteFailed(e.toString());
        } finally {
            document.close();
        }
    }
}
