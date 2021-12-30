package al.atis.api.service;

import al.atis.api.management.AppConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public abstract class RsResponseService implements Serializable {

    @Autowired
    WebRequest ui;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static final long serialVersionUID = 1L;

    public static ResponseEntity jsonResponse(Map<String, String> toJson, HttpStatus status) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = "";
        try {
            jsonStr = objectMapper.writeValueAsString(toJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(status).body(jsonStr);
    }

    public static ResponseEntity jsonResponse(HttpStatus status, String key, Object value) {
        Map<String, String> toJson = new HashMap<>();
        toJson.put(key, value.toString());
        return jsonResponse(toJson, status);
    }

    public static ResponseEntity jsonMessageResponse(HttpStatus status, Object object) {
        if (object instanceof Throwable) {
            Throwable t = (Throwable) object;
            return jsonResponse(status, AppConstants.JSON_GENERIC_MESSAGE_KEY, getErrorMessage(t));
        } else {
            return jsonResponse(status, AppConstants.JSON_GENERIC_MESSAGE_KEY, "" + object);

        }
    }

    public static ResponseEntity jsonErrorMessageResponse(Object error) {
        if (error instanceof Throwable) {
            Throwable t = (Throwable) error;
            return jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR, AppConstants.JSON_GENERIC_MESSAGE_KEY, getErrorMessage(t));
        } else {
            return jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR, AppConstants.JSON_GENERIC_MESSAGE_KEY, "" + error);
        }
    }

    private static String getErrorMessage(Throwable t) {
        String exceptionClass = t.getClass().getCanonicalName();
        return t.getMessage() == null ? exceptionClass : MessageFormat.format("{0}: {1}", exceptionClass, t.getMessage());
    }

    @SuppressWarnings("unchecked")
//    public <T> T cast(String key, Class<T> clazz) {
//        String value = ui.getParameter(key);
//        if (Long.class.equals(clazz)) {
//            return (T) Long.valueOf(value);
//        }
//        if (Integer.class.equals(clazz)) {
//            return (T) Integer.valueOf(value);
//        }
//        if (Boolean.class.equals(clazz)) {
//            return (T) Boolean.valueOf(value);
//        }
//        return (T) value;
//    }

    public <T> T cast(String key, Class<T> clazz) {
        String value = ui.getParameter(key);
        if (String.class.equals(clazz)) return (T) value;
        if (Long.class.equals(clazz)) return (T) Long.valueOf(value);

        if (Integer.class.equals(clazz)) return (T) Integer.valueOf(value);

        if (Boolean.class.equals(clazz)) return (T) Boolean.valueOf(value);

        if (LocalDateTime.class.equals(clazz)) {
            logger.info("Local Date Time: " + value);
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            return (T) LocalDateTime.parse(value, formatter);
        }


        return (T) value;
    }

    public String getUiLowercaseValue(String key) {
        return cast(key, key.getClass()) != null ? cast(key, String.class).toLowerCase() : null;
    }

    public String getUiValue(String key) {
        return ui.getParameter(key);
    }

    public Integer getUiIntegerValue(String key) {
        return Integer.valueOf(ui.getParameter(key));
    }

    public Long getUiLongValue(String key) {
        return Long.valueOf(ui.getParameter(key));
    }

    public Boolean getUiBooleanValue(String key) {
        return Boolean.valueOf(ui.getParameter(key));
    }

    public double getUiDoubleValue(String key) {
        return Double.parseDouble(ui.getParameter(key));
    }

    public LocalDateTime getUiDateTimeValue(String key) {
        String dateTimeString = ui.getParameter(key);
        logger.info("Local Date Time: " + dateTimeString);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    protected final String likeParamToLowerCase(String key) {
        logger.info("Param to Lower Case: " + cast(key, String.class).toLowerCase());
        return "%" + cast(key, String.class).toLowerCase() + "%";
    }

    protected boolean notNull(String key) {
        return ui.getParameterMap().containsKey(key) && ui.getParameter(key) != null && !ui.getParameter(key).trim().isEmpty();
    }

    protected String likeParam(String param) {
        return "%" + cast(param, String.class) + "%";
    }

    protected String likeParamL(String param) {
        return "%" + cast(param, String.class);
    }

    protected String likeParamR(String param) {
        return cast(param, String.class) + "%";
    }

    public List<String> asList(String key) {
        String value = cast(key, String.class);
        return Stream.of(value.split(",", -1)).collect(Collectors.toList());
    }

    public List<String> fromValueToList(String value) {
        return Stream.of(value.split(",", -1)).collect(Collectors.toList());
    }


}