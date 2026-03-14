package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method DELETE()
        headers {
            accept(applicationJson())
        }
        urlPath("/api/v1/shopping-carts/d71cb0ee-0632-4c8a-9dc4-7cb593b83f67/items/b14a9d78-cb93-4e96-ac96-b9f316211ddc")
    }
    response {
        status 204
    }
}