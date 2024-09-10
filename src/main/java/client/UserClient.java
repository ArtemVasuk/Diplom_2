package client;

import POJO.CreateOrderRequest;
import POJO.CreateUserRequest;
import POJO.LoginUserRequest;
import POJO.UpdateDataUserRequest;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserClient extends BaseClient{

    public static final String CREATE_URL = "/api/auth/register";
    public static final String LOGIN_URL = "/api/auth/login";
    public static final String USER_DATA_URL = "/api/auth/user";
    public static final String ORDER_URL = "/api/orders";

    @Step("Создание пользователя")
    public ValidatableResponse createUser(CreateUserRequest createUserRequest){
        return  given()
                .spec(getSpec())
                .body(createUserRequest)
                .when()
                .post(CREATE_URL)
                .then();
    }

    @Step("Логин пользователя")
    public ValidatableResponse loginUser(LoginUserRequest loginUserRequest){
        return given()
                .spec(getSpec())
                .body(loginUserRequest)
                .when()
                .post(LOGIN_URL)
                .then();
    }

    @Step("Обновление данный пользователя")
    public ValidatableResponse UpdateDataUser(String accessToken,UpdateDataUserRequest updateDataUserRequest){
        return given()
                .header("authorization", accessToken)
                .spec(getSpec())
                .body(updateDataUserRequest)
                .when()
                .patch(USER_DATA_URL)
                .then();
    }

    @Step("Получение данных пользователя")
    public ValidatableResponse getUserData(String accessToken){
        return given()
                .header("Authorization", accessToken)
                .spec(getSpec())
                .when()
                .get(USER_DATA_URL)
                .then();
    }

    @Step("Создание заказа")
    public ValidatableResponse CreateOrder(String accessToken, CreateOrderRequest createOrderRequest){
        return given()
                .header("authorization", accessToken)
                .spec(getSpec())
                .body(createOrderRequest)
                .when()
                .post(ORDER_URL)
                .then();
    }

    @Step("Получение списка заказов")
    public ValidatableResponse getListAllOrdersUser(String accessToken){
        return given()
                .header("authorization", accessToken)
                .spec(getSpec())
                .when()
                .get(ORDER_URL)
                .then();
    }

    @Step("Удаление пользователя")
    public void deleteUser(String accessToken){
        given()
                .header("authorization", accessToken)
                .spec(getSpec())
                .when()
                .delete(USER_DATA_URL);
    }
}
