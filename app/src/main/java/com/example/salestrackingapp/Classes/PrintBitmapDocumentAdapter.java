package com.example.salestrackingapp.Classes;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;

import java.io.FileOutputStream;
import java.io.IOException;

public class PrintBitmapDocumentAdapter extends PrintDocumentAdapter {


    private Bitmap bitmap;

    public PrintBitmapDocumentAdapter(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    @Override
    public void onLayout(PrintAttributes printAttributes, PrintAttributes printAttributes1, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle bundle) {
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        // Set desired print attributes here
        PrintAttributes attributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                .setResolution(new PrintAttributes.Resolution("id", "label", 300, 300))
                .build();

        PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder("file_name.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(1); // Since there is only one bitmap

        PrintDocumentInfo info = builder.build();
        callback.onLayoutFinished(info, true);
    }

    @Override
    public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        if (cancellationSignal.isCanceled()) {
            callback.onWriteCancelled();
            return;
        }

        // Ensure bitmap and output stream are valid
        try (FileOutputStream fileOutputStream = new FileOutputStream(destination.getFileDescriptor())) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
        }
    }

}
