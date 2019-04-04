freeipa-install:
  pkg.installed:
    - pkgs:
        - ntp
        - ipa-server
        - ipa-server-dns
