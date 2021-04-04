package top.yang.net.enums;

/**
 * @author lenovo
 */

public enum MediaTypes {
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
    STREAM("application/octet-stream"),
    FORM("application/x-www-form-urlencoded"),
    UPLOAD("multipart/form-data"),
    HTML("text/html");
    private final String mediaType;

    public String getMediaType() {
        return mediaType;
    }

    MediaTypes(String mediaType) {

        this.mediaType = mediaType;
    }
}
