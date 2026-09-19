// Temporary stub implementation for resetprop functionality
// This is a placeholder until prop-rs-android dependency is resolved

use anyhow::{Result, bail};
use std::collections::HashMap;

pub mod sys_prop {
    use anyhow::Result;

    pub fn init() -> Result<()> {
        // Stub: do nothing for now
        Ok(())
    }

    pub fn get_context(name: &str) -> Result<String> {
        // Stub: return the name itself as context
        Ok(name.to_string())
    }
}

pub mod resetprop {
    use super::*;
    use std::time::Duration;

    pub struct ResetProp {
        pub skip_svc: bool,
        pub persistent: bool,
        pub persist_only: bool,
        pub verbose: bool,
        pub show_context: bool,
        pub rebuild: bool,
    }

    impl ResetProp {
        pub fn get(&self, name: &str) -> Option<String> {
            // Stub: try to read from system using libc
            unsafe {
                let mut buf = [0u8; 92];
                let c_name = std::ffi::CString::new(name).ok()?;
                let len = libc::__system_property_get(
                    c_name.as_ptr(),
                    buf.as_mut_ptr() as *mut i8
                );
                if len > 0 {
                    let value = std::str::from_utf8(&buf[..len as usize]).ok()?;
                    Some(value.to_string())
                } else {
                    None
                }
            }
        }

        pub fn set(&self, name: &str, value: &str) -> Result<bool> {
            // Stub: use libc to set property
            let c_name = std::ffi::CString::new(name)?;
            let c_value = std::ffi::CString::new(value)?;
            unsafe {
                let ret = libc::__system_property_set(
                    c_name.as_ptr(),
                    c_value.as_ptr()
                );
                Ok(ret == 0)
            }
        }

        pub fn delete(&self, _name: &str) -> Result<bool> {
            // Stub: property deletion not supported in basic implementation
            bail!("Property deletion not supported in stub implementation")
        }

        pub fn list_all(&self) -> Result<Vec<(String, String)>> {
            // Stub: return empty list for now
            Ok(Vec::new())
        }

        pub fn wait(&self, _name: &str, _value: Option<&str>, _timeout: Option<Duration>) -> Result<bool> {
            // Stub: always return true immediately
            Ok(true)
        }

        pub fn load_props<I>(&self, _lines: I) -> Result<bool>
        where
            I: Iterator<Item = std::io::Result<String>>,
        {
            // Stub: do nothing
            Ok(false)
        }

        pub fn rebuild(&self, _ctx: &str) -> Result<()> {
            // Stub: do nothing
            Ok(())
        }

        pub fn rebuild_all(&self, _force: bool) -> Result<bool> {
            // Stub: do nothing
            Ok(true)
        }
    }
}
