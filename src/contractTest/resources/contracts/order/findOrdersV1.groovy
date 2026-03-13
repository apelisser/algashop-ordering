package contracts.order

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method GET()
        headers {
            accept(applicationJson())
        }
        url("/api/v1/orders") {
            queryParameters {
                parameter('page', value(stub(optional(anyNumber())), test(0)))
                parameter('size', value(stub(optional(anyNumber())), test(10)))
            }
        }
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body([
            size: fromRequest().query('size'),
            number: 0,
            totalElements: 2,
            totalPages: 1,
            content: [
                [
                    id: "0PRC7KBEZ3CD3",
                    customer: [
                        id: anyUuid(),
                        firstName: anyNonBlankString(),
                        lastName: anyNonBlankString(),
                        email: anyNonBlankString(),
                        document: anyNonBlankString(),
                        phone: anyNonBlankString()
                    ],
                    totalItems: anyInteger(),
                    totalAmount: anyNumber(),
                    placedAt: anyIso8601WithOffset(),
                    paidAt: null,
                    canceledAt: null,
                    readyAt: null,
                    status: anyNonBlankString(),
                    paymentMethod: anyNonBlankString(),
                ],
                [
                    id: "0PRC7NDPZ3EVJ",
                    customer: [
                        id: anyUuid(),
                        firstName: anyNonBlankString(),
                        lastName: anyNonBlankString(),
                        email: anyNonBlankString(),
                        document: anyNonBlankString(),
                        phone: anyNonBlankString()
                    ],
                    totalItems: anyInteger(),
                    totalAmount: anyNumber(),
                    placedAt: anyIso8601WithOffset(),
                    paidAt: null,
                    canceledAt: null,
                    readyAt: null,
                    status: anyNonBlankString(),
                    paymentMethod: anyNonBlankString(),
                ]
            ]
        ])
    }
}