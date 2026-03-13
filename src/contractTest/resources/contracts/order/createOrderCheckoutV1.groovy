package contracts.order

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method POST()
        urlPath("/api/v1/orders")
        headers {
            accept(applicationJson())
            contentType('application/vnd.order-with-shopping-cart.v1+json')
        }
        body([
            shoppingCartId: value(
                stub(anyUuid()),
                test("163f33bb-8d27-49f1-b235-ff513658b179")
            ),
            paymentMethod: value(
                stub(anyNonBlankString()),
                test("GATEWAY_BALANCE")
            ),
            shipping: [
                recipient: [
                    firstName: value(
                        stub(anyNonBlankString()),
                        test("John")
                    ),
                    lastName: value(
                        stub(anyNonBlankString()),
                        test("Doe")
                    ),
                    document: value(
                        stub(anyNonBlankString()),
                        test("12345")
                    ),
                    phone: value(
                        stub(anyNonBlankString()),
                        test("5511912341234")
                    )
                ],
                address: [
                    street: value(
                        stub(anyNonBlankString()),
                        test("Bourbon Street")
                    ),
                    number: value(
                        stub(anyNonBlankString()),
                        test("2000")
                    ),
                    complement: value(
                        stub(optional(anyNonBlankString())),
                        test("apt 122")
                    ),
                    neighborhood: value(
                        stub(anyNonBlankString()),
                        test("North Ville")
                    ),
                    city: value(
                        stub(anyNonBlankString()),
                        test("Yostfort")
                    ),
                    state: value(
                        stub(anyNonBlankString()),
                        test("South Carolina")
                    ),
                    zipCode: value(
                        stub(anyNonBlankString()),
                        test("12321")
                    )
                ]
            ],
            billing: [
                firstName: value(
                    stub(anyNonBlankString()),
                    test("John")
                ),
                lastName: value(
                    stub(anyNonBlankString()),
                    test("Doe")
                ),
                document: value(
                    stub(anyNonBlankString()),
                    test("12345")
                ),
                email: value(
                    stub(anyNonBlankString()),
                    test("john.doe@example.com")
                ),
                phone: value(
                    stub(anyNonBlankString()),
                    test("5511912341234")
                ),
                address: [
                    street: value(
                        stub(anyNonBlankString()),
                        test("Bourbon Street")
                    ),
                    number: value(
                        stub(anyNonBlankString()),
                        test("2000")
                    ),
                    complement: value(
                        stub(optional(anyNonBlankString())),
                        test("apt 122")
                    ),
                    neighborhood: value(
                        stub(anyNonBlankString()),
                        test("North Ville")
                    ),
                    city: value(
                        stub(anyNonBlankString()),
                        test("Yostfort")
                    ),
                    state: value(
                        stub(anyNonBlankString()),
                        test("South Carolina")
                    ),
                    zipCode: value(
                        stub(anyNonBlankString()),
                        test("12321")
                    )
                ]
            ]
        ])
    }
    response {
        status 201
        headers {
            contentType(applicationJson())
        }
        body([
            id: "01226N0640J7Q",
            customer: [
                id: anyUuid(),
                firstName: anyNonBlankString(),
                lastName: anyNonBlankString(),
                email: anyNonBlankString(),
                document: anyNonBlankString(),
                phone: anyNonBlankString(),
            ],
            totalItems: anyInteger(),
            totalAmount: anyNumber(),
            placedAt: anyIso8601WithOffset(),
            paidAt: null,
            canceledAt: null,
            readyAt: null,
            status: "PLACED",
            paymentMethod: fromRequest().body('$.paymentMethod'),
            shipping: [
                cost: anyNumber(),
                expectedDate: anyDate(),
                recipient: [
                    firstName: fromRequest().body('$.shipping.recipient.firstName'),
                    lastName: fromRequest().body('$.shipping.recipient.lastName'),
                    document: fromRequest().body('$.shipping.recipient.document'),
                    phone: fromRequest().body('$.shipping.recipient.phone'),
                ],
                address: [
                    street: fromRequest().body('$.shipping.address.street'),
                    number: fromRequest().body('$.shipping.address.number'),
                    complement: fromRequest().body('$.shipping.address.complement'),
                    neighborhood: fromRequest().body('$.shipping.address.neighborhood'),
                    city: fromRequest().body('$.shipping.address.city'),
                    state: fromRequest().body('$.shipping.address.state'),
                    zipCode: fromRequest().body('$.shipping.address.zipCode'),
                ],
            ],
            billing: [
                firstName: fromRequest().body('$.billing.firstName'),
                lastName: fromRequest().body('$.billing.lastName'),
                document: fromRequest().body('$.billing.document'),
                email: fromRequest().body('$.billing.email'),
                phone: fromRequest().body('$.billing.phone'),
                address: [
                    street: fromRequest().body('$.billing.address.street'),
                    number: fromRequest().body('$.billing.address.number'),
                    complement: fromRequest().body('$.billing.address.complement'),
                    neighborhood: fromRequest().body('$.billing.address.neighborhood'),
                    city: fromRequest().body('$.billing.address.city'),
                    state: fromRequest().body('$.billing.address.state'),
                    zipCode: fromRequest().body('$.billing.address.zipCode'),
                ]
            ],
        ])
    }
}