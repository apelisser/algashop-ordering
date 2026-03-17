package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method POST()
        headers {
            contentType(applicationJson())
            accept(applicationJson())
        }
        urlPath("/api/v1/shopping-carts")
        body([
            customerId: value(
                stub(anyUuid()),
                test("1e7f075c-ede5-49a0-a389-ba81052f45fb")
            )
        ])
    }
    response {
        status 201
        body([
            id: anyUuid(),
            customerId: fromRequest().body('$.customerId'),
            totalItems: 3,
            totalAmount: anyNumber(),
            items: [
                [
                    id: anyUuid(),
                    productId: anyUuid(),
                    name: "Notebook",
                    price: anyNumber(),
                    quantity: 1,
                    totalAmount: anyNumber(),
                    available: anyBoolean()
                ],
                [
                    id: anyUuid(),
                    productId: anyUuid(),
                    name: "Desktop",
                    price: anyNumber(),
                    quantity: 2,
                    totalAmount: anyNumber(),
                    available: anyBoolean()
                ]
            ]
        ])
    }
}