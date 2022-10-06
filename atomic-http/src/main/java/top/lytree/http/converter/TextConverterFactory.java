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


final class TextConverterFactory extends Converter.Factory {

    private TextConverterFactory() {
    }

    public static TextConverterFactory create() {
        return new TextConverterFactory();
    }
    @Override
    public Converter<?, RequestBody> requestBodyConverter(@NotNull Type type, @NotNull Annotation[] parameterAnnotations, @NotNull Annotation[] methodAnnotations,
            @NotNull Retrofit retrofit) {
        return new TextRequestBodyConverter();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(@NotNull Type type, @NotNull Annotation[] annotations, @NotNull Retrofit retrofit) {
        return new TextResponseBodyConverter();
    }




}

final class TextRequestBodyConverter implements Converter<String, RequestBody> {

    private static final MediaType MEDIA_TYPE = MediaType.get("text/plain; charset=UTF-8");


    TextRequestBodyConverter() {

    }

    @Override
    public RequestBody convert(@NotNull String value) {
        return RequestBody.create(value, MEDIA_TYPE);
    }
}

final class TextResponseBodyConverter implements Converter<ResponseBody, String> {


    TextResponseBodyConverter() {

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