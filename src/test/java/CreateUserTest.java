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

public class CreateUserTest {
    private UserClient userClient = new UserClient();
    private String accessToken;

    @Test
    @DisplayName("Проверка создания пользователя")
    @Description("Должен создаться пользователь")
    public void userShouldBeCreated(){
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        ValidatableResponse response = userClient.createUser(createUserRequest)
                .statusCode(SC_OK);
        accessToken = response.extract().path("accessToken");
    }

    @Test
    @DisplayName("Проверка создания пользователя, который уже существует")
    @Description("При повторном создании пользователя должен возвращаться код ошибки 403")
    public void repeatedUserShouldBeCreated(){
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        userClient.createUser(createUserRequest);
        userClient.createUser(createUserRequest)
                .statusCode(SC_FORBIDDEN)
                .body("success",Matchers.equalTo(false))
                .body("message", Matchers.equalTo("User already exists"));
    }

    @Test
    @DisplayName("Проверка создания пользователя без e-mail")
    @Description("При создании пользователя без e-mail должен возвращаться код ошибки 403 и в теле ответа сообщение \"Email, password and name are required fields\"")
    public void createUserWithoutEmail(){
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        createUserRequest.setEmail("");
        userClient.createUser(createUserRequest)
                .statusCode(SC_FORBIDDEN)
                .body("success",Matchers.equalTo(false))
                .body("message",Matchers.equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Проверка создания пользователя без пароля")
    @Description("При создании пользователя без пароля должен возвращаться код ошибки 403 и в теле ответа сообщение \"Email, password and name are required fields\"")
    public void createUserWithoutPassword(){
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        createUserRequest.setPassword("");
        userClient.createUser(createUserRequest)
                .statusCode(SC_FORBIDDEN)
                .body("success",Matchers.equalTo(false))
                .body("message",Matchers.equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Проверка создания пользователя без имени")
    @Description("При создании пользователя без имени должен возвращаться код ошибки 403 и в теле ответа сообщение \"Email, password and name are required fields\"")
    public void createUserWithoutName(){
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        createUserRequest.setName("");
        userClient.createUser(createUserRequest)
                .statusCode(SC_FORBIDDEN)
                .body("success",Matchers.equalTo(false))
                .body("message",Matchers.equalTo("Email, password and name are required fields"));
    }

    @After
    @DisplayName("Удаление пользователя")
    public void tearDown(){
        if (accessToken != null){
            userClient.deleteUser(accessToken);
        }
    }
}
