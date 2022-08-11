namespace smithy.example

use aws.protocols#awsQueryCompatible
use aws.protocols#awsQueryError
use aws.protocols#awsJson1_0

@awsQueryCompatible(
    "AccessDeniedException": {
        "code": "AccessDenied"
    },
    "QueueDoesNotExist": {
        "code": "AWS.SimpleQueueService.NonExistentQueue",
    }
)
@awsJson1_0
service MyService {
    version: "2020-02-05"
}
