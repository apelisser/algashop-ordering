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
            shoppingCartId: null,
            shipping: [
                recipient: [
                    firstName: " "
                ],
                address: null
            ],
            billing: [
                firstName: " ",
                address: [
                    street: " "
                ]
            ]
        ])
    }
    response {
        status 400
        headers {
            contentType('application/problem+json')
        }
        body([
            instance: fromRequest().path(),
            type: "/errors/invalid-fields",
            title: "Invalid fields",
            detail: "One or more fields are invalid",
            fields: [
                "shoppingCartId": anyNonBlankString(),
                "paymentMethod": anyNonBlankString(),
                "shipping.recipient.firstName": anyNonBlankString(),
                "shipping.recipient.lastName": anyNonBlankString(),
                "shipping.recipient.document": anyNonBlankString(),
                "shipping.recipient.phone": anyNonBlankString(),
                "shipping.address": anyNonBlankString(),
                "billing.firstName": anyNonBlankString(),
                "billing.lastName": anyNonBlankString(),
                "billing.email": anyNonBlankString(),
                "billing.document": anyNonBlankString(),
                "billing.phone": anyNonBlankString(),
                "billing.address.number": anyNonBlankString(),
                "billing.address.neighborhood": anyNonBlankString(),
                "billing.address.state": anyNonBlankString(),
                "billing.address.city": anyNonBlankString(),
                "billing.address.zipCode": anyNonBlankString(),
                "billing.address.street": anyNonBlankString(),
            ]
        ])
    }
}