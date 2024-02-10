package com.example.webapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScreenshotUtils {

    
    public static void captureScreenshotAndSaveAsPDF(View view, String fileName, Context context) {
        view.setDrawingCacheEnabled(true);

        try{
            int topExcludeHeight = 200;
            int heightWithExclusion = view.getHeight() - topExcludeHeight;

            Bitmap screenshot = Bitmap.createBitmap(view.getDrawingCache(), 0, topExcludeHeight, view.getWidth(), heightWithExclusion);

            view.setDrawingCacheEnabled(false);

            // Convert bitmap to PDF
            openPdfFromBitmap(screenshot, fileName, context);
        }
        catch (Exception ex){

            Toast.makeText(context, ""+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    private static void openPdfFromBitmap(Bitmap bitmap, String fileName, Context context) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        canvas.drawBitmap(bitmap, 0, 0, null);

        document.finishPage(page);

        try {
            File cacheDir = context.getExternalCacheDir();
            if (cacheDir != null) {
                File pdfFile = new File(cacheDir, fileName + ".pdf");
                FileOutputStream outputStream = new FileOutputStream(pdfFile);
                document.writeTo(outputStream);
                outputStream.flush();
                outputStream.close();
                document.close();

                // Open the PDF using an Intent
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                context.startActivity(intent);
            } else {
                // Handle the case where cacheDir is null
                Toast.makeText(context, "Cache directory is not available", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private static void createPdfFromBitmap(Bitmap bitmap, String fileName, Context context) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        canvas.drawBitmap(bitmap, 0, 0, null);

        document.finishPage(page);

        File folder = new File(Environment.getExternalStorageDirectory() + "/");


        File file = new File(folder, fileName + ".pdf");
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            document.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
            document.close();
           Toast.makeText(context, "Saved file in StorageDirectory", Toast.LENGTH_LONG).show();
            openPdfFile(file,context);




        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, ""+e, Toast.LENGTH_LONG).show();

        }
    }

    private static void openPdfFile(File pdfFile, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(pdfFile);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "No application available to view PDF", Toast.LENGTH_SHORT).show();
        }
    }
}
