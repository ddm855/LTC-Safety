//
//  LTCConcernDetailViewController.m
//  LTC Safety
//
//  Created by Allan Kerr on 2017-01-26.
//  Modified by Daniel Morris
//  Copyright © 2017 CS371 Group 2. All rights reserved.
//

#import "LTCConcernDetailViewController.h"
#import "LTCClientApi.h"
#import "LTCConcernStatus+CoreDataClass.h"
#import "GTLRClient.h"
#import "LTCConcern_Testing.h"
#import "LTCConcernViewModel.h"
#import "LTCLoadingViewController.h"

NSString *const LTCDetailConcernTitle = @"DETAIL_CONCERN_TITLE";
NSString *const LTCDetailConcernEdit = @"DETAIL_EDIT_CONCERN";
NSString *const LTCDetailRetractConfirmation = @"DETAIL_RETRACT_COMFIRMATION";
NSString *const LTCDetailRetractError = @"DETAIL_RETRACT_ERROR";

@interface LTCConcernDetailViewController ()
@property (readwrite, nonatomic, strong) LTCConcernDetailViewModel *viewModel;
@property (nonatomic, strong) NSNotificationCenter *notificationCenter;
@property (nonatomic, strong) LTCClientApi *clientApi;
@end

@implementation LTCConcernDetailViewController
@dynamic viewModel;

- (LTCConcernDetailViewModel *)viewModel {
    NSAssert([self.form isKindOfClass:[LTCConcernDetailViewModel class]], @"Unexpected type for %@ form.", self.class);
    // Cast the form to a detail view model
    return (LTCConcernDetailViewModel *)self.form;
}

- (instancetype)initWithViewModel:(LTCConcernDetailViewModel *)viewModel {
    if (self = [super initWithForm:viewModel]) {
        self.clientApi = [[LTCClientApi alloc] init];
        self.notificationCenter = [NSNotificationCenter defaultCenter];
        self.title = NSLocalizedString(@"View Concern", nil);
        // set the view model retract callback
        self.viewModel.retractCallback = @selector(_retractConcern);

    }
    NSAssert(self.viewModel, @"%@ initializer completed with a nil view model.", self.class);
    NSAssert1(self != nil, @"Failed to initialize %@", self.class);
    return self;
}

/**
 The callback for when the retract concern button is clicked causing a request to be sent to the backend requesting that the concern status be changed to retracted.
 */
- (void)_retractConcern {
    
    // Display the loading spinner to the user until the retract call has finished
    LTCLoadingViewController *loadingMessage = [LTCLoadingViewController configure];
    [self presentViewController: loadingMessage animated: true completion: nil];
    
    // Call the retract concern endpoint on the Client API using the view model's concern's owner token
    [self.clientApi retractConcern:self.viewModel.concern.ownerToken
                        completion:^(GTLRClient_UpdateConcernStatusResponse *concernStatus, NSError *error){
        if(error == nil){
            [self.notificationCenter postNotificationName:LTCUpdatedConcernStatusNotification
                                                   object:self
                                                 userInfo:@{@"status": concernStatus}];
        }
        [loadingMessage dismissViewControllerAnimated:YES completion:^(){
            UIAlertController *alert;
            if (error != nil){
                NSString *errorMessage = [error.userInfo valueForKey:@"error"];
                alert = [UIAlertController alertControllerWithTitle:NSLocalizedString(LTCDetailRetractError, nil)
                                                            message:errorMessage
                                                     preferredStyle:UIAlertControllerStyleAlert];
                UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:NSLocalizedString(@"OK", nil)
                                                                       style:UIAlertActionStyleCancel
                                                                     handler:nil];
                [alert addAction:cancelAction];
                [self presentViewController:alert
                                   animated:YES
                                 completion:nil];
                
                XLFormRowDescriptor *row = [self.viewModel formRowWithTag:@"RETRACT_CONCERN"];
                [self deselectFormRow:row];
            }else {
                [super.navigationController popViewControllerAnimated:YES];
            }
        }];
    }];
}

@end
