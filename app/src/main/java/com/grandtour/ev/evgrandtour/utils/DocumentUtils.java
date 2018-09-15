package com.grandtour.ev.evgrandtour.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;

public final class DocumentUtils {

    private DocumentUtils() { }

    @NonNull
    public static String readJSONFromUri(@NonNull Context context, @NonNull Uri uri) throws IOException {
        String json = "";
        InputStream inputStream = context.getContentResolver()
                    .openInputStream(uri);
        if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                inputStream.close();
                json = builder.toString();
        }
        return json;
    }
}
