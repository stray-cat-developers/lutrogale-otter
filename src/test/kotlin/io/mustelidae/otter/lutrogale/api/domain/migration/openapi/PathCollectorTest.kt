package io.mustelidae.otter.lutrogale.api.domain.migration.openapi

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class PathCollectorTest {

    val v3Json = """
        {
           "openapi":"3.0.1",
           "info":{
              "title":"OpenAPI definition",
              "version":"v1"
           },
           "servers":[
              {
                 "url":"http://localhost:4300",
                 "description":"Generated server url"
              }
           ],
           "paths":{
              "/v1/account":{
                 "put":{
                    "tags":[
                       "Account"
                    ],
                    "summary":"change password",
                    "operationId":"changePassword",
                    "requestBody":{
                       "content":{
                          "application/json":{
                             "schema":{
                                "${'$'}\ref":"#/components/schemas/ChangePassword"
                             }
                          }
                       },
                       "required":true
                    },
                    "responses":{
                       "500":{
                          "description":"Internal Server Error",
                          "content":{
                             "*/*":{
                                "schema":{
                                   "${'$'}\ref":"#/components/schemas/Boilerplate.Error"
                                }
                             }
                          }
                       },
                       "200":{
                          "description":"OK",
                          "content":{
                             "*/*":{
                                "schema":{
                                   "${'$'}\ref":"#/components/schemas/BirmanCat.Common.Reply"
                                }
                             }
                          }
                       }
                    }
                 },
                 "post":{
                    "tags":[
                       "Account"
                    ],
                    "summary":"sign up",
                    "operationId":"signUp",
                    "requestBody":{
                       "content":{
                          "application/json":{
                             "schema":{
                                "${'$'}\ref":"#/components/schemas/BirmanCat.Account.Request.SignUp"
                             }
                          }
                       },
                       "required":true
                    },
                    "responses":{
                       "500":{
                          "description":"Internal Server Error",
                          "content":{
                             "*/*":{
                                "schema":{
                                   "${'$'}\ref":"#/components/schemas/Boilerplate.Error"
                                }
                             }
                          }
                       },
                       "200":{
                          "description":"OK",
                          "content":{
                             "*/*":{
                                "schema":{
                                   "${'$'}\ref":"#/components/schemas/BirmanCat.Common.Reply"
                                }
                             }
                          }
                       }
                    }
                 }
              },
              "/v1/account/me":{
                 "get":{
                    "tags":[
                       "Account"
                    ],
                    "summary":"Get Account (Session Attribute)",
                    "operationId":"findOne",
                    "responses":{
                       "500":{
                          "description":"Internal Server Error",
                          "content":{
                             "*/*":{
                                "schema":{
                                   "${'$'}\ref":"#/components/schemas/Boilerplate.Error"
                                }
                             }
                          }
                       },
                       "400":{
                          "description":"Bad Request",
                          "content":{
                             "*/*":{
                                "schema":{
                                   "${'$'}\ref":"#/components/schemas/Boilerplate.Error"
                                }
                             }
                          }
                       },
                       "200":{
                          "description":"OK",
                          "content":{
                             "*/*":{
                                "schema":{
                                   "${'$'}\ref":"#/components/schemas/BirmanCat.Common.Reply"
                                }
                             }
                          }
                       }
                    }
                 },
                 "put":{
                    "tags":[
                       "Account"
                    ],
                    "summary":"self modify account",
                    "operationId":"modify",
                    "requestBody":{
                       "content":{
                          "application/json":{
                             "schema":{
                                "${'$'}\ref":"#/components/schemas/SelfModify"
                             }
                          }
                       },
                       "required":true
                    },
                    "responses":{
                       "500":{
                          "description":"Internal Server Error",
                          "content":{
                             "*/*":{
                                "schema":{
                                   "${'$'}\ref":"#/components/schemas/Boilerplate.Error"
                                }
                             }
                          }
                       },
                       "200":{
                          "description":"OK",
                          "content":{
                             "*/*":{
                                "schema":{
                                   "${'$'}\ref":"#/components/schemas/BirmanCat.Common.Reply"
                                }
                             }
                          }
                       }
                    }
                 }
              }
           },
           "components":{
              "schemas":{
                 "Boilerplate.Error":{
                    "required":[
                       "code",
                       "message",
                       "timestamp",
                       "type"
                    ],
                    "type":"object",
                    "properties":{
                       "timestamp":{
                          "type":"string",
                          "description":"error occurred time"
                       },
                       "code":{
                          "type":"string",
                          "description":"error code"
                       }
                    }
                 },
                 "BirmanCat.Common.Reply":{
                    "type":"object",
                    "description":"Http Json Response Base Format (Class 형태의 리소스를 반환할 때 사용)"
                 },
                 "SelfModify":{
                    "type":"object",
                    "properties":{
                       "department":{
                          "type":"string"
                       },
                       "phone":{
                          "type":"string"
                       }
                    }
                 }
              }
           }
        }
    """.trimIndent()

    @Test
    fun collectTest() {
        val swaggerSpec = SwaggerSpec(v3Json, SwaggerSpec.Type.JSON)
        val openApi = swaggerSpec.openAPI

        // Given
        val pathCollector = PathCollector(openApi)

        // When
        val pathWithMethods = pathCollector.collectPathAndMethods()

        // Then
        pathWithMethods.size shouldBe 2
    }
}
