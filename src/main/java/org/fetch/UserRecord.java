package org.fetch;

import lombok.Data;

@Data
public class UserRecord {
    String user_id;
    String app_version;
    String device_type;
    String ip;
    String locale;
    String device_id;

}
