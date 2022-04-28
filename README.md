# MailingList

An application, which fetches e-mails of the mailing list [abelana](https://lists.riseup.net/www/info/abelana) and displays them.
In future other mailing lists may follow.

- The mails are presented as a paginated post feed.
- Images attached to emails are presented in a gallery.
- The answer and add buttons redirect to the default mail client and fill in known information.
- Posts are collapsed as default, because some e-mails can be quite long.

<img src="https://user-images.githubusercontent.com/38131809/165557674-d44426f8-9ef2-4a31-8c72-ca8bc2c5603b.png" width="250" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://user-images.githubusercontent.com/38131809/165553461-bcc609a5-5972-4520-a296-30f78210d679.png" width="250" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://user-images.githubusercontent.com/38131809/165553636-d69e7e8e-9c28-4eb9-a265-5e09cd73072d.png" width="250" />

## Technical background

- In the background an extra e-mail account is used.
- E-mails are fetched with [Jakarta Api](https://jakarta.ee/specifications/platform/9/apidocs/) (former Java Mail Api).
- Parsing of the e-mails is done in the app. So far there is no backend.
