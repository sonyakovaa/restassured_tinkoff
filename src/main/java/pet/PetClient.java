package pet;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface PetClient {

    @RequestLine("POST /")
    @Headers("Content-Type: application/json")
    void createPet(Pet pet);

    @RequestLine("GET /{petId}")
    Pet findById(@Param("petId") long petId);
}
