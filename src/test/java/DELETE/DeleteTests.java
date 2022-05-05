package DELETE;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
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

public class DeleteTests {
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

    // Предварительно создаем питомца, удаляем и проверяем, что удаление произошло успешно
    @ParameterizedTest
    @MethodSource("params")
    public void deletePet(Pet pet) {
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
    }

    // Удаление несуществующего питомца
    @ParameterizedTest
    @MethodSource("params")
    public void deleteNonExistentPet(Pet pet) {
        PetClient petClient = Feign.builder()
                .decoder(new JacksonDecoder())
                .encoder(new JacksonEncoder()).target(PetClient.class, baseURI);

        petClient.createPet(pet);

        delete("/" + pet.getId())
                .then()
                .statusCode(200);

        delete("/" + pet.getId())
                .then()
                .statusCode(404);
    }
}
