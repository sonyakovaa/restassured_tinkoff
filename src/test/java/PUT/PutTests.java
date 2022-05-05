package PUT;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pet.Category;
import pet.Pet;
import pet.PetClient;

import java.util.stream.Stream;

import static io.restassured.RestAssured.*;

public class PutTests {
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

    // Создаем питомца и проверяем, что создание произошло успешно, и обновляем ему имя
    @ParameterizedTest
    @MethodSource("params")
    public void putPet(Pet pet) {
        PetClient petClient = Feign.builder()
                .decoder(new JacksonDecoder())
                .encoder(new JacksonEncoder()).target(PetClient.class, baseURI);

        petClient.createPet(pet);

        get("/" + pet.getId())
                .then()
                .statusCode(200);

        pet.setName("Tuzik");

        given()
                .body(pet)
                .contentType(ContentType.JSON)
                .put()
                .then()
                .statusCode(200);
    }

    // Обновление имени несуществующему питомцу. Тест не проходит, так как PUT срабатывает, как POST, и возвращает 200
    @ParameterizedTest
    @MethodSource("params")
    public void putNonExistentPet(Pet pet) {
        PetClient petClient = Feign.builder()
                .decoder(new JacksonDecoder())
                .encoder(new JacksonEncoder()).target(PetClient.class, baseURI);

        petClient.createPet(pet);

        delete("/" + pet.getId())
                .then()
                .statusCode(200);

        get("/" + pet.getId())
                .then()
                .statusCode(404);

        pet.setName("Tuzik");

        given()
                .body(pet)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .contentType(ContentType.JSON)
                .put()
                .then()
                .statusCode(404);
    }
}
