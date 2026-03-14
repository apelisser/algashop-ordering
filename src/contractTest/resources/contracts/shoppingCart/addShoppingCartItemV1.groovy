package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method POST()
        headers {
            contentType(applicationJson())
            accept(applicationJson())
        }
        urlPath("/api/v1/shopping-carts/d71cb0ee-0632-4c8a-9dc4-7cb593b83f67/items")
        body([
            quantity: 1,
            productId: "6a9e55f9-4b86-4bc2-aa5d-7eb963a30160"
        ])
    }
    response {
        status 204
    }
}