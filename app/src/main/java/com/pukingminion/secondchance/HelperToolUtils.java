package com.pukingminion.secondchance;

import android.content.Context;
import android.text.TextUtils;

import junit.framework.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samarth on 13/05/17.
 */

public class HelperToolUtils {

    public static String[] getAllFilesInAssetByExtension(Context context, String path, String extension){
        Assert.assertNotNull(context);

        try {
            String[] files = context.getAssets().list(path);

            if(TextUtils.isEmpty(extension)){
                return files;
            }

            List<String> filesWithExtension = new ArrayList<String>();

            for(String file : files){
                if(file.endsWith(extension)){
                    filesWithExtension.add(file);
                }
            }

            return filesWithExtension.toArray(new String[filesWithExtension.size()]);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
}
