package pet;

import feign.Headers;
import feign.RequestLine;

public interface PetClient {

    @RequestLine("POST /")
    @Headers("Content-Type: application/json")
    void createPet(Pet pet);
}
