package userprovider;

import POJO.CreateUserRequest;
import org.apache.commons.lang3.RandomStringUtils;

public class UserProvider {
    public static CreateUserRequest getRandomCreateUserRequest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setEmail(RandomStringUtils.randomAlphabetic(8)+"@yandex.ru");
        createUserRequest.setPassword(RandomStringUtils.randomAlphabetic(8));
        createUserRequest.setName(RandomStringUtils.randomAlphabetic(8));
        return createUserRequest;
    }
}
