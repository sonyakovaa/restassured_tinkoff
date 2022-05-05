package PUT;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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

        return Stream.of(dog).map(Arguments::of);
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

        String petName = "Tuzik";
        pet.setName(petName);

        given()
                .body(pet)
                .contentType(ContentType.JSON)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .put()
                .then()
                .statusCode(200);

        Assertions.assertEquals(petName, pet.getName());
    }

    // Обновление имени несуществующему питомцу. PUT срабатывает, как POST, и возвращает 200
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

        pet.setName("Tuzik");

        given()
                .body(pet)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .contentType(ContentType.JSON)
                .put()
                .then()
                .statusCode(200);
    }
}
