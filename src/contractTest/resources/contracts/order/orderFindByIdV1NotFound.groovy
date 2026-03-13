package contracts.order

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method GET()
        headers {
            accept(applicationJson())
        }
        url("/api/v1/orders/01226N0693HDH")
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