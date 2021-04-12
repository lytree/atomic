package top.yang.net.enums;

import okhttp3.MediaType;

/**
 * @author lenovo
 */

public enum ContentTypes {
    //MediaTypes类型
    JSON("application/json; charset=utf-8"),
    IMG("application/x-img"),
    PDF("application/pdf"),
    XML("application/xml; charset=utf-8"),
    DOC("application/msword"),
    PNG("image/png"),
    JPG("image/jpeg"),
    JPEG("image/jpeg"),
    WAV("audio/wav"),
    MP3("audio/mp3"),
    MP4("video/mpeg4"),
    TXT("text/plain"),
    XLS("application/x-xls"),
    ZIP("application/zip"),
    APK("application/vnd.android.package-archive"),
    STREAM("application/octet-stream"),
    FORM("application/x-www-form-urlencoded"),
    UPLOAD("multipart/form-data"),
    HTML("text/html");
    private final String mediaType;

    public MediaType getMediaType() {
        return MediaType.parse(mediaType);
    }

    ContentTypes(String mediaType) {
        this.mediaType = mediaType;
    }
}
