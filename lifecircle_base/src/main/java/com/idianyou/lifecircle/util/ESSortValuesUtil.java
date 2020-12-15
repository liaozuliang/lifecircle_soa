package com.idianyou.lifecircle.util;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/12/15 11:31
 */
@Slf4j
public class ESSortValuesUtil {

    public static String encodeSortValues(Object[] sortValues) {
        String str = null;

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(sortValues);

            byte[] bytes = byteArrayOutputStream.toByteArray();
            str = Base64.getEncoder().encodeToString(bytes);

            byteArrayOutputStream.close();
            objectOutputStream.close();
        } catch (Exception e) {
            log.error("ESSortValuesUtil.encodeSortValues error:", e);
        }

        return str;
    }

    public static Object[] decodeSortValues(String sortValuesStr) {
        Object[] sortValues = null;

        try {
            byte[] bytes = Base64.getDecoder().decode(sortValuesStr);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            sortValues = (Object[]) objectInputStream.readObject();

            objectInputStream.close();
            byteArrayInputStream.close();
        } catch (Exception e) {
            log.error("ESSortValuesUtil.decodeSortValues error:", e);
        }

        return sortValues;
    }


}
