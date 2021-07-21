package com.android.sdrive.DataBase.Tables;

public class FileStorage {

    public static String TABLE_NAME = "filestorage"; //Table Name

    public static String Id= "id";
    public static String File_ID= "fileid";
    public static String File_Name= "filename";
    public static String File_Path = "filepath";
    public static String File_Type = "filetype";
    public static String File_Size = "filesize";
    public static String File_Upload_Status = "fileuploadstatus";
    public static String File_Upload_Time = "fileuploadtime";
    public static String TimeStamp= "timestamp";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + Id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + File_ID + " TEXT NOT NULL,"
                    + File_Name + " TEXT NOT NULL,"
                    + File_Path + " TEXT NOT NULL,"
                    + File_Type + " TEXT NOT NULL,"
                    + File_Size + " TEXT NOT NULL,"
                    + File_Upload_Status + " TEXT NOT NULL,"
                    + File_Upload_Time + " TEXT,"
                    + TimeStamp + " TEXT"
                    + ")";


}
