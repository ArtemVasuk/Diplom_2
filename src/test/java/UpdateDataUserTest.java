import POJO.CreateUserRequest;
import POJO.UpdateDataUserRequest;
import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import userprovider.UserProvider;
import static org.apache.http.HttpStatus.*;

public class UpdateDataUserTest {
    private UserClient userClient = new UserClient();
    private String accessToken;
    private String existEmail;
    private String existName;

    @Test
    @DisplayName("Проверка обновления данных авторизованного пользователя")
    @Description("Должен вернуться статус код 200 в теле сообщения обновленные данные пользователя")
    public void updateDataUserAuthorized() {
        UpdateDataUserRequest updateDataUserRequest = new UpdateDataUserRequest(RandomStringUtils.randomAlphabetic(8) + "@mail.ru", RandomStringUtils.randomAlphabetic(8));
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        ValidatableResponse response = userClient.createUser(createUserRequest);
        accessToken = response.extract().path("accessToken");
        userClient.UpdateDataUser(accessToken, updateDataUserRequest).log().all()
                .statusCode(SC_OK)
                .body("success", Matchers.equalTo(true))
                .body("user", Matchers.notNullValue());
    }

    @Test
    @DisplayName("Проверка обновления данных неавторизованного пользователя")
    @Description("Должен вернуться статус код ошибки 401 в теле сообщение \"You should be authorised\"")
    public void updateDataUserNotAuthorized() {
        UpdateDataUserRequest updateDataUserRequest = new UpdateDataUserRequest(RandomStringUtils.randomAlphabetic(8) + "@mail.ru", RandomStringUtils.randomAlphabetic(8));
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        userClient.createUser(createUserRequest);
        userClient.UpdateDataUser("", updateDataUserRequest)
                .statusCode(SC_UNAUTHORIZED).log().all()
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message", Matchers.equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Проверка обновления e-mail авторизованного пользователя")
    @Description("Должен вернуться статус код 200 в теле сообщения обновленный e-mail пользователя")
    public void updateDataEmail() {
        UpdateDataUserRequest updateDataUserRequest = new UpdateDataUserRequest(RandomStringUtils.randomAlphabetic(8) + "@mail.ru", existName);
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        ValidatableResponse response = userClient.createUser(createUserRequest);
        accessToken = response.extract().path("accessToken");
        existName = response.extract().path(createUserRequest.getName());
        userClient.UpdateDataUser(accessToken, updateDataUserRequest)
                .statusCode(SC_OK)
                .body("success", Matchers.equalTo(true));
    }

    @Test
    @DisplayName("Проверка обновления имени авторизованного пользователя")
    @Description("Должен вернуться статус код 200 в теле сообщения обновленное имя пользователя")
    public void updateDataName() {
        UpdateDataUserRequest updateDataUserRequest = new UpdateDataUserRequest(existEmail, RandomStringUtils.randomAlphabetic(8));
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        ValidatableResponse response = userClient.createUser(createUserRequest);
        accessToken = response.extract().path("accessToken");
        existEmail = createUserRequest.getEmail();
        userClient.UpdateDataUser(accessToken, updateDataUserRequest)
                .statusCode(SC_OK)
                .body("success", Matchers.equalTo(true));
    }

    @After
    @DisplayName("Удаление пользователя")
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
