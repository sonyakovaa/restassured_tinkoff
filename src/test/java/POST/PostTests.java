package POST;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pet.Category;
import pet.Pet;

import java.util.stream.Stream;

import static io.restassured.RestAssured.*;

public class PostTests {

    public static Stream<Arguments> params() {
        Pet dog = new Pet();
        dog.setId(1);
        dog.setName("Lacho");
        Category dogs = new Category();
        dogs.setName("Dog");
        dog.setCategory(dogs);
        dog.setStatus("available");

        Pet cat = new Pet();
        cat.setId(2);
        cat.setName("Biba");
        Category cats = new Category();
        cats.setName("Cat");
        cat.setCategory(cats);
        cat.setStatus("available");

        Pet rat = new Pet();
        rat.setId(3);
        rat.setName("Boba");
        Category rats = new Category();
        rats.setName("Rat");
        rat.setCategory(rats);
        rat.setStatus("available");

        return Stream.of(dog, cat, rat).map(Arguments::of);
    }

    @BeforeAll
    public static void connectingURI() {
        baseURI = "https://petstore.swagger.io/v2/pet";
    }

    // Создаем питомца и проверяем, что создание произошло успешно
    @ParameterizedTest
    @MethodSource("params")
    public void createPet(Pet pet) {
        given()
                .body(pet)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .statusCode(200);

        get("/" + pet.getId())
                .then()
                .statusCode(200);
    }
}
