import POJO.CreateOrderRequest;
import POJO.CreateUserRequest;
import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import userprovider.UserProvider;
import java.util.ArrayList;
import java.util.HashMap;

import static org.apache.http.HttpStatus.*;

public class OrdersListUserTest {
    private UserClient userClient = new UserClient();
    private String accessToken;
    private String HashIngredients;

    @Before
    @DisplayName("Получение хэша ингредиентов для теста")
    public void getHashIngredients() {
        ValidatableResponse getHashIngredientsRequest = userClient.getHashIngredients("");
        ArrayList ListIngredients = getHashIngredientsRequest.extract().path("data");
        HashMap HashMapIngredients = (HashMap) ListIngredients.get(0);
        HashIngredients = (String) HashMapIngredients.get("_id");
    }

    @Test
    @DisplayName("Проверка получения списка заказов авторизованного пользователя")
    @Description("Должен возвращаться статус код 200 и в теле ответа должен отображаться заказ")
    public void getListOrdersAuthorizedUser(){
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        CreateOrderRequest createOrderRequest= new CreateOrderRequest();
        createOrderRequest.setIngredients(HashIngredients);
        ValidatableResponse response = userClient.createUser(createUserRequest);
        accessToken = response.extract().path("accessToken");
        userClient.CreateOrder(accessToken, createOrderRequest);
        userClient.getListAllOrdersUser(accessToken)
                .statusCode(SC_OK)
                .body("success", Matchers.equalTo(true))
                .and()
                .body("orders",Matchers.notNullValue())
                .body("total",Matchers.notNullValue())
                .body("totalToday",Matchers.notNullValue());
    }

    @Test
    @DisplayName("Проверка получения списка заказов неавторизованного пользователя")
    @Description("Должен возвращаться статус код 401 и в теле ответа сообщение \"You should be authorised\"")
    public void getListOrdersWithoutAuthorizedUser(){
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        CreateOrderRequest createOrderRequest;
        userClient.createUser(createUserRequest);
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setIngredients(HashIngredients);
        userClient.CreateOrder("", createOrderRequest);
        userClient.getListAllOrdersUser("")
                .statusCode(SC_UNAUTHORIZED)
                .body("success",Matchers.equalTo(false))
                .and()
                .body("message",Matchers.equalTo("You should be authorised"));
    }

    @After
    @DisplayName("Удаление пользователя")
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
