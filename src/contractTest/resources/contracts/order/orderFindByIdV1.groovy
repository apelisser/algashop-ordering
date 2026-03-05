package contracts.order

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method GET()
        headers {
            accept 'application/json'
        }
        url('/order/v1/orders')
    }
    response {
        status 200
    }
}