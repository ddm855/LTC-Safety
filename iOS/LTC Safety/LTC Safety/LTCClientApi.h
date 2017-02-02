//
//  LTCClientApi.h
//  LTC Safety
//
//  Created by Allan Kerr on 2017-01-27.
//  Copyright © 2017 CS371 Group 2. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LTCConcern+CoreDataClass.h"

typedef void(^LTCSubmitConcernCompletion)(GTLRClient_SubmitConcernResponse *concernResponse, NSError *error);
typedef void(^LTCRetractConcernCompletion)(NSError *error);

@interface LTCClientApi : NSObject
- (void)submitConcern:(GTLRClient_ConcernData *)concern completion:(LTCSubmitConcernCompletion)completion;
- (void)retractConcern:(NSString *)ownerToken completion:(LTCRetractConcernCompletion)completion;
@end
