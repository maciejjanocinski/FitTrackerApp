package app.stripe;

import lombok.Data;

@Data
 class WebhookData {
    private String type;
    private EventData data;
}

@Data
class EventData {
    private ObjectData object;

}

@Data
class ObjectData {
    private String id;
    private String customer;
    private String subscription;
}