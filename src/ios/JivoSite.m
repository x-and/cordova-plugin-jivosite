/********* JivoSite.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import <Cordova/CDVPlugin.h>

#import "SRWebViewController.h"
@import UIKit;

@interface CDVPlugin (Private)

- (instancetype)initWithWebViewEngine:(id <CDVWebViewEngineProtocol>)theWebViewEngine;

@end


@interface JivoSite : CDVPlugin

@property (readonly) SRWebViewController *webViewController;

- (void)open_chat:(CDVInvokedUrlCommand*)command;

@end


@implementation JivoSite

@synthesize webViewController = _webViewController;

- (instancetype)initWithWebViewEngine:(id <CDVWebViewEngineProtocol>)theWebViewEngine {
    self = [super initWithWebViewEngine:theWebViewEngine];
    if ( !self ) {
        return self;
    }
    _webViewController = [SRWebViewController new];
    return self;
}

- (void)open_chat:(CDVInvokedUrlCommand*)command {

    NSInteger userId = [command.arguments[1] integerValue];
    NSString *title = command.arguments[2];

    self.webViewController.navigationTitle = title;
    self.webViewController.userId = userId;
    [self.webViewController showWebViewController];

    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK]
                                callbackId:command.callbackId];
}

@end
