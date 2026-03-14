package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method GET()
        headers {
            accept(applicationJson())
        }
        urlPath("/api/v1/shopping-carts/a90fd4bd-037a-43b7-9fba-011f3f5df99d")
    }
    response {
        status 404
        headers {
            contentType('application/problem+json')
        }
        body([
            instance: fromRequest().path(),
            type: "/errors/not-found",
            title: "Not found"
        ])
    }
}