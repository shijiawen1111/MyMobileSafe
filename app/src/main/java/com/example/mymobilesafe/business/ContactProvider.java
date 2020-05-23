package com.example.mymobilesafe.business;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import com.example.mymobilesafe.bean.ContactBean;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JW.S on 2020/5/22 5:36 PM.
 */
public class ContactProvider {
    /**
     * 获取系统全部联系人
     *
     * @param context
     * @return
     */
    public static List<ContactBean> getAllContacts(Context context) {
        List<ContactBean> list = new ArrayList<>();
        // 数据查询--》系统的联系人
        ContentResolver resolver = context.getContentResolver();
        // 通过号码查询
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        // 要查询的列
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,//名称
                ContactsContract.CommonDataKinds.Phone.NUMBER,      //号码
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID}; //联系人id
        //要查询的列
        String selection = null;//要查询的条件
        String[] selectionArgs = null;//查询条件对应的参数
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " desc";//查询的排序
        Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, sortOrder);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                String number = cursor.getString(1);
                long contactId = cursor.getLong(2);
                ContactBean bean = new ContactBean();
                bean.name = name;
                bean.number = number;
                bean.contactId = contactId;
                list.add(bean);
            }
            cursor.close();
        }
        return list;
    }

    /**
     * 获取联系人图片
     * @param context
     * @param contactId
     * @return
     */
    public static Bitmap getContactPhoto(Context context,long contactId) {
        ContentResolver cr = context.getContentResolver();
        Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactId));
        InputStream stream = ContactsContract.Contacts.openContactPhotoInputStream(cr, contactUri);
        // 将流转换为bitmap
        return BitmapFactory.decodeStream(stream);
    }
}
