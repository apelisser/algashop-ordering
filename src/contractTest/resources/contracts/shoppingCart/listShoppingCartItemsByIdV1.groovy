package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method GET()
        headers {
            accept(applicationJson())
        }
        urlPath("/api/v1/shopping-carts/d71cb0ee-0632-4c8a-9dc4-7cb593b83f67/items")
    }
    response {
        status 200
        body([
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