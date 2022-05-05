package GET;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
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

public class GetTests {
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

    // Создаем питомца и проверяем, что он создался, и поля созданного питомца совпадают с поданным на вход
    @Test
    public void getAndValidatePet() {
        Pet cow = new Pet();
        cow.setId(4);
        cow.setName("Muuu");
        Category cows = new Category();
        cows.setName("Cows");
        cow.setCategory(cows);
        cow.setStatus("available");

        PetClient petClient = Feign.builder()
                .decoder(new JacksonDecoder())
                .encoder(new JacksonEncoder()).target(PetClient.class, baseURI);

        petClient.createPet(cow);

        get("/" + cow.getId())
                .then()
                .statusCode(200);

        Pet checkPet = petClient.findById(cow.getId());

        Assertions.assertEquals(cow.getId(), checkPet.getId());
        Assertions.assertEquals(cow.getName(), checkPet.getName());
        Assertions.assertEquals(cow.getCategory().getName(), checkPet.getCategory().getName());
        Assertions.assertEquals(cow.getStatus(), checkPet.getStatus());
    }

    // Получение несуществующего (удаленного) питомца
    @ParameterizedTest
    @MethodSource("params")
    public void getNonExistentPet(Pet pet) {
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
}
