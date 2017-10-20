package org.exoplatform.intercom.rs;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.IdentityManagerImpl;
import org.json.JSONObject;

import javax.annotation.security.RolesAllowed;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Path("intercom/settings")
@Produces(MediaType.APPLICATION_JSON)
public class IntercomSettings implements ResourceContainer {

    private static Log log = ExoLogger.getLogger(IntercomSettings.class);

    private OrganizationService orgService_;

    private UserHandler userHandler;

    private IdentityManager identityManager;

    private static String USER_ID_KEY = "user_id";
    private static String USER_NAME_KEY = "user_name";
    private static String USER_EMAIL_KEY = "user_email";
    private static String USER_SIGNIN_DATE_KEY = "user_signin_date";
    private static String INTERCOM_APP_ID_KEY = "APP_ID";
    private static String INTERCOM_HMAC_KEY = "HMAC";
    /**
     * This property holds app_id'value to enable connection with Intercom respond module
     */
    private static String INTERCOM_SETTINGS_APP_ID = "intercom.settings.appid";

    /**
     * This property holds secret_key's value to enable Intercom identity verification
     */
    private static String INTERCOM_SETTINGS_SECRET = "intercom.settings.secretkey";

    private static final CacheControl cacheControl = new CacheControl();

    static {
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);
    }

    public IntercomSettings() {
        super();
        orgService_ = CommonsUtils.getService(OrganizationService.class);
        userHandler = orgService_.getUserHandler();
        identityManager = CommonsUtils.getService(IdentityManagerImpl.class);
    }

    @GET
    @Path("/respond")
    @RolesAllowed("users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response respondSettings() throws Exception {
        JSONObject jsonGlobal = new JSONObject();
        JSONObject jsonUser = new JSONObject();
        try {
            org.exoplatform.services.security.Identity currentIdentity = ConversationState.getCurrent().getIdentity();
            String userName = currentIdentity.getUserId();
            User user = userHandler.findUserByName(userName);
            Identity socialIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userName, false);
            Profile currentProfile = socialIdentity.getProfile();
            //--- Set Intercom app_id
            jsonUser.put(INTERCOM_APP_ID_KEY, PropertyManager.getProperty(INTERCOM_SETTINGS_APP_ID));
            //--- Set Intercom to enable identity verification
            jsonUser.put(INTERCOM_HMAC_KEY, generateHMAC(userName, PropertyManager.getProperty(INTERCOM_SETTINGS_SECRET)));
            //--- Set Username
            jsonUser.put(USER_ID_KEY, user.getUserName());
            //--- Set Display Name
            jsonUser.put(USER_NAME_KEY, user.getDisplayName());
            //--- Set email
            jsonUser.put(USER_EMAIL_KEY, user.getEmail());
            //--- Set last login time
            jsonUser.put(USER_SIGNIN_DATE_KEY, user.getLastLoginTime().getTime());

            jsonGlobal.put("user", jsonUser);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Response.ok(jsonGlobal.toString(), MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
    }

    /**
     * Generate hash MAC
     * @param userid
     * @param secret
     * @return String
     */
    private String generateHMAC(String userid, String secret) {
        Mac sha256_HMAC = null;
        StringBuffer result = new StringBuffer();
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = (sha256_HMAC.doFinal(userid.getBytes()));
            for (byte b : hash) {
                result.append(String.format("%02X", b));
            }
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        } catch (InvalidKeyException e) {
            log.error(e.getMessage(), e);
        }
        return result.toString();
    }
}
