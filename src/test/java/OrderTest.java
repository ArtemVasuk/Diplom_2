import POJO.CreateOrderRequest;
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

public class OrderTest {
    private UserClient userClient = new UserClient();
    private String accessToken;

    @Test
    @DisplayName("Проверка создания заказа неавторизованным пользователем")
    @Description("Должен вернуться код ошибки 401, так как в описании сказано, что заказ может сделать только авторизованный пользователь")
    public void createOrderWithoutAuthorization() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setIngredients("61c0c5a71d1f82001bdaaa6d");
        userClient.CreateOrder("", createOrderRequest)
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Проверка создания заказа авторизованным пользователем")
    @Description("Должен вернуться статус код 200 и в теле информация о заказе")
    public void createOrderAuthorization() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        CreateOrderRequest createOrderRequest;
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setIngredients("61c0c5a71d1f82001bdaaa6d");
        ValidatableResponse response = userClient.createUser(createUserRequest);
        accessToken = response.extract().path("accessToken");
        userClient.CreateOrder(accessToken, createOrderRequest)
                .statusCode(SC_OK)
                .body("success", Matchers.equalTo(true))
                .and()
                .body("name",Matchers.notNullValue())
                .body("order",Matchers.notNullValue());
    }

    @Test
    @DisplayName("Проверка создания заказа авторизованным пользователем без ингредиентов")
    @Description("Должен вернуться код ошибки 400 и в теле сообщение \"Ingredient ids must be provided\" ")
    public void createOrderWithoutIngredient() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setIngredients(null);
        ValidatableResponse response = userClient.createUser(createUserRequest);
        accessToken = response.extract().path("accessToken");
        userClient.CreateOrder(accessToken, createOrderRequest)
                .statusCode(SC_BAD_REQUEST)
                .body("success",Matchers.equalTo(false))
                .and()
                .body("message",Matchers.equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Проверка создания заказа с неверным хэшем ингредиента")
    @Description("Должен вернуться код ошибки 500 Internal Server Error")
    public void createOrderInvalidHashIngredient() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setIngredients("невалидный ингредиент");
        ValidatableResponse response = userClient.createUser(createUserRequest);
        accessToken = response.extract().path("accessToken");
        userClient.CreateOrder(accessToken, createOrderRequest)
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @After
    @DisplayName("Удаление пользователя")
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
