package top.lytree.http.converter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class HTMLConverterFactory extends Converter.Factory {

    private HTMLConverterFactory() {
    }

    public static HTMLConverterFactory create() {
        return new HTMLConverterFactory();
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(@NotNull Type type, @NotNull Annotation[] parameterAnnotations, @NotNull Annotation[] methodAnnotations,
            @NotNull Retrofit retrofit) {
        return new HTMLRequestBodyConverter();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(@NotNull Type type, @NotNull Annotation[] annotations, @NotNull Retrofit retrofit) {
        return new HTMLResponseBodyConverter();
    }


}

final class HTMLRequestBodyConverter implements Converter<String, RequestBody> {

    private static final MediaType MEDIA_TYPE = MediaType.get("text/html; charset=utf-8");


    HTMLRequestBodyConverter() {

    }

    @Override
    public RequestBody convert(@NotNull String value) {
        return RequestBody.create(value, MEDIA_TYPE);
    }
}

final class HTMLResponseBodyConverter implements Converter<ResponseBody, String> {


    HTMLResponseBodyConverter() {

    }

    @Override
    public String convert(ResponseBody value) throws IOException {
        try {
            return value.string();
        } finally {
            value.close();
        }
    }
}