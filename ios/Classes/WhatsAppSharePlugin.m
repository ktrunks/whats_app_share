#import "WhatsAppSharePlugin.h"
#if __has_include(<whats_app_share/whats_app_share-Swift.h>)
#import <whats_app_share/whats_app_share-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "whats_app_share-Swift.h"
#endif

@implementation WhatsAppSharePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftWhatsAppSharePlugin registerWithRegistrar:registrar];
}
@end
