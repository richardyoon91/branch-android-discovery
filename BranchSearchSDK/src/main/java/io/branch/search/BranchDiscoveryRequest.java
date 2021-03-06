package io.branch.search;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Request model for Branch Discovery.
 */
@SuppressWarnings("unchecked")
public class BranchDiscoveryRequest<T extends BranchDiscoveryRequest> {

    enum JSONKey {
        Latitude("user_latitude"),
        Longitude("user_longitude"),
        Timestamp("utc_timestamp"),
        /** we want to override the configuration-level extras */
        Extra(BranchConfiguration.JSONKey.RequestExtra.toString());

        JSONKey(String key) {
            _key = key;
        }

        @NonNull
        @Override
        public String toString() {
            return _key;
        }

        private String _key;
    }

    // Latitude of the user
    private double user_latitude;

    // Longitude for the user
    private double user_longitude;

    private final Map<String, Object> extra = new HashMap<>();

    /**
     * Private Constructor.
     */
    BranchDiscoveryRequest() {
    }

    /**
     * Set the current location.
     * Branch Search needs location permission for better search experience.  Call this method when location updates happen.
     * @param location Location
     * @return this BranchDiscoveryRequest
     */
    public T setLocation(Location location) {
        if (location != null) {
            setLatitude(location.getLatitude());
            setLongitude(location.getLongitude());
        }
        return (T) this;
    }

    /**
     * Set the current location - latitude.
     * @param latitude latitude
     * @return this BranchDiscoveryRequest
     */
    @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
    public T setLatitude(double latitude) {
        this.user_latitude = latitude;
        return (T) this;
    }

    /**
     * Set the current location - longitude.
     * @param longitude latitude
     * @return this BranchDiscoveryRequest
     */
    @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
    public T setLongitude(double longitude) {
        this.user_longitude = longitude;
        return (T) this;
    }

    /**
     * Adds extra data to be passed to server in form
     * of a key-value pair. This value will override any other value for the
     * same key that was previously set and values that were set at the configuration
     * level using {@link BranchConfiguration#addRequestExtra(String, Object)}.
     *
     * Passing null as a value will clear any extra that was previously set in this request
     * (but not those set at the configuration level).
     *
     * @param key a key
     * @param data value
     * @return this BranchDiscoveryRequest
     */
    @SuppressWarnings("WeakerAccess")
    public T setExtra(@NonNull String key, @Nullable Object data) {
        if (data == null) {
            extra.remove(key);
        } else {
            extra.put(key, data);
        }
        return (T) this;
    }

    JSONObject convertToJson(JSONObject jsonObject) {
        try {
            jsonObject.putOpt(JSONKey.Latitude.toString(), user_latitude);
            jsonObject.putOpt(JSONKey.Longitude.toString(), user_longitude);

            // Add the current timestamp.
            Long tsLong = System.currentTimeMillis();
            jsonObject.putOpt(JSONKey.Timestamp.toString(), tsLong);

            // Add extra data.
            // The JSONObject for this key might already exist because the key is shared
            // between this class and BranchConfiguration.
            if (!extra.keySet().isEmpty()) {
                JSONObject extraData = jsonObject.optJSONObject(JSONKey.Extra.toString());
                if (extraData == null) extraData = new JSONObject();

                for (String key : extra.keySet()) {
                    Object value = extra.get(key);
                    extraData.putOpt(key, value);
                }
                jsonObject.putOpt(JSONKey.Extra.toString(), extraData);
            }


        } catch (JSONException ignore) {
        }
        return jsonObject;
    }
}
