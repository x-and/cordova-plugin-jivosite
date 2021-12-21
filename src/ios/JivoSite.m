/********* JivoSite.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import <Cordova/CDVPlugin.h>

#import "SRWebViewController.h"
@import UIKit;

@interface CDVPlugin (Private)

- (instancetype)initWithWebViewEngine:(id <CDVWebViewEngineProtocol>)theWebViewEngine;

@end


@interface JivoSite : CDVPlugin

@property (readonly) SRWebNavigationController<SRWebViewController *> *webNavigationViewController;

- (void)open_chat:(CDVInvokedUrlCommand*)command;

@end


@implementation JivoSite

@synthesize webNavigationViewController = _webNavigationViewController;

- (instancetype)initWithWebViewEngine:(id <CDVWebViewEngineProtocol>)theWebViewEngine {
    self = [super initWithWebViewEngine:theWebViewEngine];
    if ( !self ) {
        return self;
    }
    _webNavigationViewController = [SRWebNavigationController defaultNavigationController];
    return self;
}

- (void)open_chat:(CDVInvokedUrlCommand*)command {
    NSInteger userId = [command.arguments[0] integerValue];
    NSString *title = command.arguments[2];
    NSString *payload = command.arguments[3];

    self.webNavigationViewController.visibleViewController.navigationTitle = title;
    self.webNavigationViewController.visibleViewController.userId = userId;
    self.webNavigationViewController.visibleViewController.payload = payload;
    [self.webNavigationViewController showWebViewController];

    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK]
                                callbackId:command.callbackId];
}

@end
