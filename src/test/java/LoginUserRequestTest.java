import POJO.CreateUserRequest;
import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import userprovider.UserProvider;
import static org.apache.http.HttpStatus.*;

public class LoginUserRequestTest {
    private UserClient userClient = new UserClient();
    private String accessToken;

    @Test
    @DisplayName("Проверка что пользователь может авторизироваться")
    @Description("Должен вернуться статус код 200 и в теле ответа должен вернуться accessToken и refreshToken")
    public void loginUser() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        ValidatableResponse response = userClient.createUser(createUserRequest);
        accessToken = response.extract().path("accessToken");
        userClient.loginUser(POJO.LoginUserRequest.from(createUserRequest))
                .statusCode(SC_OK)
                .body("success", Matchers.equalTo(true))
                .body("accessToken", Matchers.notNullValue())
                .body("refreshToken", Matchers.notNullValue());
    }

    @Test
    @DisplayName("Проверка логина с несуществующим e-mail")
    @Description("Должен вернуться код ошибки 401 и в теле сообщение \"email or password are incorrect\"")
    public void loginNonExistentUser() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        userClient.createUser(createUserRequest);
        createUserRequest.setEmail("напtter@mail.ru");
        userClient.loginUser(POJO.LoginUserRequest.from(createUserRequest))
                .statusCode(SC_UNAUTHORIZED)
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message",Matchers.equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Проверка логина с несуществующим паролем")
    @Description("Должен вернуться код ошибки 401 и в теле сообщение \"email or password are incorrect\"")
    public void loginNonExistentPassword() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        userClient.createUser(createUserRequest);
        createUserRequest.setPassword("234624612");
        userClient.loginUser(POJO.LoginUserRequest.from(createUserRequest))
                .statusCode(SC_UNAUTHORIZED)
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message",Matchers.equalTo("email or password are incorrect"));
    }

    @After
    @DisplayName("Удаление пользователя")
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
